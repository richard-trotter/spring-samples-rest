package rht.samples.spring.rest;

public class CatalogEntry {

  public CatalogEntry(String title, String author, String publisher, String releaseDate, String isbn) {
    super();
    this.title = title;
    this.author = author;
    this.publisher = publisher;
    this.releaseDate = releaseDate;
    this.isbn = isbn;
  }

  private String title, author, publisher, releaseDate, isbn;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public String getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(String releaseDate) {
    this.releaseDate = releaseDate;
  }

  public String getISBN() {
    return isbn;
  }

  public void setISBN(String isbn) {
    this.isbn = isbn;
  }
}
