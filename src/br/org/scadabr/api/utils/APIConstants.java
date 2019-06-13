package br.org.scadabr.api.utils;

import br.org.scadabr.api.constants.ErrorCode;
import br.org.scadabr.api.vo.APIError;

public interface APIConstants {
	public static final int MAX_READ_DATA_RETURN = 2000;
	public static final int MAX_DATA_HISTORY_RETURN = 5000;
	public static final int MAX_BROWSE_TAGS_RETURN = 4000;
	public static final int MAX_ACTIVE_EVENTS_RETURN = 2000;
	public static final int MAX_EVENTS_HISTORY_RETURN = 5000;

	public static final String NO_READ_TAG = "Nenhuma TAG definida para leitura.";
	public static final String NO_WRITE_TAG = "Nenhuma TAG definida para escrita.";

	public static final String ACCESS_DENIED = "Usuário não possui permissão necessária.";

	public static final String UNKNOW_TAG_NAME = "Nenhuma TAG corresponde ao nome fornecido.";
	public static final String NON_EXISTENT_TAG = "A TAG fornecida não existe no sistema.";

	public static final String INVALID_PARAMETER = "Parametro Inválido.";
	public static final String INSUFFICIENT_PARAMETER = "Faltam parâmetros obrigatórios.";

	public static final String NO_REGISTERED_DATASOURCE = "Nenhum DataSource registrado no sistema.";

	public static final String NO_REGISTERED_DATAPOINT = "Nenhum DataPoint registrado no sistema.";

	public static final String CONDITION_NOT_IMPLEMENTED = "Condição não implementada.";

	public static final String THERE_ARE_MORE_RETURN_VALUES = "Existem mais valores para retorno.";

	public static final String INVALID_ID = "ID Inválido.";
	public static final String UNSPECIFIED_ERROR = "Erro desconhecido.";

	public static final APIError ERROR_OK = new APIError(ErrorCode.OK,
			"Nenhum erro ocorreu.");

}
