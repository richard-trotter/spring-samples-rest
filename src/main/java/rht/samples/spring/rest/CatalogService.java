package rht.samples.spring.rest;

import java.util.ArrayList;
import java.util.List;

public class CatalogService implements ICatalogService {

  private static final CatalogEntry SampleCatalogEntry = new CatalogEntry(
          "Getting Started with OAuth 2.0", 
          "Ryan Boyd", 
          "O'Reilly Media, Inc.", 
          "February 2012",
          "9781449311605");

  public List<CatalogEntry> getAllEntries() {

    CatalogEntry candidate = SampleCatalogEntry;
    List<CatalogEntry> allEntries = new ArrayList<CatalogEntry>();
    allEntries.add(candidate);
    return allEntries;
  }

  public CatalogEntry getEntry(String isbn) throws EntryNotFoundException {

    CatalogEntry candidate = SampleCatalogEntry;

    if (candidate.getISBN().equals(isbn))
      return candidate;

    throw new EntryNotFoundException("Catalog Entry for isbn='" + isbn + "' Not Found");
  }

}
