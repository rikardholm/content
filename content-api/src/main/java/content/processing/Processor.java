package content.processing;

public interface Processor<OUTPUT> {
    Session<OUTPUT> template(String path);
}
