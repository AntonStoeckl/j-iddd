package domain.customer;

import domain.customer.command.ChangeCustomerEmailAddress;
import domain.customer.command.ChangeCustomerName;
import domain.customer.command.ConfirmCustomerEmailAddress;
import domain.customer.command.RegisterCustomer;
import domain.customer.event.*;

import java.util.List;

public final class Customer {
    public static CustomerRegistered register(RegisterCustomer command) {
        return CustomerRegistered.build(
                command.customerID,
                command.emailAddress,
                command.confirmationHash,
                command.name
        );
    }

    public static List<Event> confirmEmailAddress(List<Event> eventStream, ConfirmCustomerEmailAddress command) {
        CurrentState state = CurrentState.reconstitute(eventStream);

        if (!state.confirmationHash.equals(command.confirmationHash)) {
            return List.of(
                    CustomerEmailAddressConfirmationFailed.build(command.customerID)
            );
        }

        if (state.isEmailAddressConfirmed) {
            return List.of();
        }

        return List.of(
                CustomerEmailAddressConfirmed.build(command.customerID)
        );
    }

    public static List<Event> changeEmailAddress(List<Event> eventStream, ChangeCustomerEmailAddress command) {
        CurrentState state = CurrentState.reconstitute(eventStream);

        if (command.emailAddress.equals(state.emailAddress)) {
            return List.of();
        }

        return List.of(
                CustomerEmailAddressChanged.build(command.customerID, command.emailAddress, command.confirmationHash)
        );
    }

    public static List<Event> changeName(List<Event> eventStream, ChangeCustomerName command) {
        CurrentState state = CurrentState.reconstitute(eventStream);

        if (command.name.equals(state.name)) {
            return List.of();
        }

        return List.of(
                CustomerNameChanged.build(command.customerID, command.name)
        );
    }
}

