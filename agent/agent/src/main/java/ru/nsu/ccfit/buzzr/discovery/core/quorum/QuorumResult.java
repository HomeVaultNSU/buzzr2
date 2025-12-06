package ru.nsu.ccfit.buzzr.discovery.core.quorum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class QuorumResult<T> {

    private final Status status;

    private final T response;

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public static <U> QuorumResult<U> failedQuorum() {
        return new QuorumResult<>(Status.FAILED, null);
    }

    public static <U> QuorumResult<U> successQuorum() {
        return new QuorumResult<>(Status.SUCCESS, null);
    }

    public enum Status {
        SUCCESS, FAILED
    }
}