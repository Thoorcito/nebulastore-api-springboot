package cl.thoorcito.nebulastore.domain.exception;


public class ExceedsBuildVolumeException extends RuntimeException {
    public ExceedsBuildVolumeException(String message) {
        super(message);
    }
}