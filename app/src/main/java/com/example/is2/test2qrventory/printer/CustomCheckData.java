package com.example.is2.test2qrventory.printer;

public class CustomCheckData {

	private String text;
	private boolean isChecked;

	public CustomCheckData(String text, boolean isChecked) {
		this.text = text;
		this.isChecked = isChecked;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean getChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

}
