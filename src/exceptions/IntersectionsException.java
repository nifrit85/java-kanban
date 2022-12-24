package exceptions;

import constants.Constants;

public class IntersectionsException extends RuntimeException {
    public IntersectionsException(int id) {
        super(Constants.INTERSECTION_MESSAGE + id);
    }
}
