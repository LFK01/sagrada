package it.polimi.se2018.view;

import it.polimi.se2018.model.events.messages.Message;

public interface ThreadCompleteListener {
    public void notifyOfThreadComplete(final Thread thread, Message message);
}
