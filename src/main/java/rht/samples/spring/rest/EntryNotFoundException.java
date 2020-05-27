package rht.samples.spring.rest;

public class EntryNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;

  public EntryNotFoundException(String msg) {
    super(msg);
  }
}
