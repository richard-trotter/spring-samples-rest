package rht.samples.spring.rest;

import java.util.List;

public interface ICatalogService {

  List<CatalogEntry> getAllEntries();

  CatalogEntry getEntry(String isbn) throws EntryNotFoundException;

}