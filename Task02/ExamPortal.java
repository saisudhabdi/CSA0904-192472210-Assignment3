import java.io.IOException;

class ExamException extends Exception {
    ExamException(String message) { super(message); }
    ExamException(String message, Throwable cause) { super(message, cause); }
}

class ExamWindowClosedException extends ExamException {
    ExamWindowClosedException(String message) { super(message); }
}

class StudentNotRegisteredException extends ExamException {
    StudentNotRegisteredException(String message) { super(message); }
}

class SubmissionStorageException extends ExamException {
    SubmissionStorageException(String message, Throwable cause) { super(message, cause); }
}

class SubmissionTooLargeException extends RuntimeException {
    SubmissionTooLargeException(String message) { super(message); }
}

class SubmissionStorage {
    void save(String fileName, long sizeKb) throws SubmissionStorageException {
        try {
            if (sizeKb > 500) throw new IOException("Disk quota exceeded");
            System.out.println("Saved " + fileName);
        } catch (IOException e) {
            throw new SubmissionStorageException("Could not store " + fileName, e);
        }
    }
}

class ExamService {
    private final SubmissionStorage storage = new SubmissionStorage();

    void submit(String studentId, boolean registered, boolean examOpen,
                String fileName, long sizeKb) throws ExamException {
        if (!examOpen) throw new ExamWindowClosedException("Exam window has closed.");
        if (!registered) throw new StudentNotRegisteredException("Student is not registered.");
        if (sizeKb > 1000) throw new SubmissionTooLargeException("Answer file exceeds 1000 KB.");
        storage.save(fileName, sizeKb);
        System.out.println("Submission accepted for " + studentId);
    }
}

public class ExamPortal {
    public static void main(String[] args) {
        ExamService service = new ExamService();
        submitCase(service, "STU101", true, true, "answer01.pdf", 120);
        submitCase(service, "STU102", false, true, "answer02.pdf", 100);
        submitCase(service, "STU103", true, false, "answer03.pdf", 100);
        submitCase(service, "STU104", true, true, "answer04.pdf", 800);

        try {
            service.submit("STU105", true, true, "answer05.pdf", 1200);
        } catch (ExamException | SubmissionTooLargeException e) {
            System.out.println("Controller message: " + e.getMessage());
        }
    }

    private static void submitCase(ExamService service, String id, boolean registered,
                                   boolean open, String file, long size) {
        try {
            service.submit(id, registered, open, file, size);
        } catch (ExamException e) {
            System.out.println("Controller message: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("Cause: " + e.getCause().getMessage());
            }
        } catch (SubmissionTooLargeException e) {
            System.out.println("Controller message: " + e.getMessage());
        }
    }
}