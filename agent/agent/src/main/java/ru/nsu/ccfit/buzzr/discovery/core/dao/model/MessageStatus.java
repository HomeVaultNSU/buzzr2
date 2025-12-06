package ru.nsu.ccfit.buzzr.discovery.core.dao.model;

import java.util.Comparator;

public enum MessageStatus {

    NEW,

    PROCESSED,

    OUTDATED;

    public static final Comparator<MessageStatus> COMPARATOR =
            Comparator.comparingInt(s ->
                    switch (s) {
                        case PROCESSED -> 3;
                        case OUTDATED -> 2;
                        case NEW -> 1;
                    }
            );
}