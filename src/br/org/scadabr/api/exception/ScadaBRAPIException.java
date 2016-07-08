package br.org.scadabr.api.exception;

import br.org.scadabr.api.vo.APIError;

public class ScadaBRAPIException extends Exception {
	private APIError error;

	public ScadaBRAPIException(APIError error) {
		this.error = error;
	}

	public void setError(APIError error) {
		this.error = error;
	}

	public APIError getError() {
		return error;
	}

}
