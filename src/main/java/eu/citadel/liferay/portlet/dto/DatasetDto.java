package eu.citadel.liferay.portlet.dto;

import java.io.File;

import eu.citadel.converter.data.dataset.Dataset;

/**
 * @author ttrapanese
 */
public class DatasetDto {
	private String file;
	private File fileEntry;
	private boolean isFirstRowHeader;
	private Integer sheetNumber;
	private String delimiter;
	private String quote;
	private String newline;
	private Dataset dataset;
	private Integer itemNumber;
	
	public String getQuote() {
		return quote;
	}
	public void setQuote(String quote) {
		this.quote = quote;
	}
	public String getNewline() {
		return newline;
	}
	public void setNewline(String newline) {
		this.newline = newline;
	}

	
	public boolean isFirstRowHeader() {
		return isFirstRowHeader;
	}
	public void setFirstRowHeader(boolean isFirstRowHeader) {
		this.isFirstRowHeader = isFirstRowHeader;
	}
	public Integer getSheetNumber() {
		return sheetNumber;
	}
	public void setSheetNumber(Integer sheetNumber) {
		this.sheetNumber = sheetNumber;
	}
	public String getDelimiter() {
		return delimiter;
	}
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	public Dataset getDataset() {
		return dataset;
	}
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	
	public String getFirstRowValue(){
		if (isFirstRowHeader()) {
			return "label";
		} else {
			return "data";
		}
	}
	public Integer getItemNumber() {
		return itemNumber;
	}
	public void setItemNumber(Integer itemNumber) {
		this.itemNumber = itemNumber;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public File getFileEntry() {
		return fileEntry;
	}
	public void setFileEntry(File fileEntry) {
		this.fileEntry = fileEntry;
	}
	
}
