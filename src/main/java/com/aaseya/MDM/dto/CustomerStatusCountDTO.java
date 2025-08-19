package com.aaseya.MDM.dto;

public class CustomerStatusCountDTO {
	private long pending;
	private long completed;
	private long rejected;
	private long totalCases;
	private long in_progress;

	public CustomerStatusCountDTO(long in_progress, long pending, long completed, long rejected, long totalCases) {
		this.in_progress = in_progress;
		this.pending = pending;
		this.completed = completed;
		this.rejected = rejected;
		this.totalCases = totalCases;
	}

	public long getPending() {
		return pending;
	}

	public void setPending(long pending) {
		this.pending = pending;
	}

	public long getCompleted() {
		return completed;
	}

	public void setCompleted(long completed) {
		this.completed = completed;
	}

	public long getRejected() {
		return rejected;
	}

	public void setRejected(long rejected) {
		this.rejected = rejected;
	}

	public long getTotalCases() {
		return totalCases;
	}

	public void setTotalCases(long totalCases) {
		this.totalCases = totalCases;
	}

	public long getIn_progress() {
		return in_progress;
	}

	public void setIn_progress(long in_progress) {
		this.in_progress = in_progress;
	}



}
