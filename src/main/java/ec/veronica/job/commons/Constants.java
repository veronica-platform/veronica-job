package ec.veronica.job.commons;

public interface Constants {

    String ERROR_ATTRIBUTE = "error";
    int MAX_ACCESS_KEY_LENGTH = 49;
    String XML_XPATH_EMAILS = "//infoAdicional/campoAdicional[@nombre='%s']";
    String XML_XPATH_DOC_NUMBER = "//infoTributaria/secuencial";
    String XML_XPATH_COD_DOC = "//infoTributaria/codDoc";
    String XML_XPATH_SUPPLIER_NAME = "//infoTributaria/razonSocial";
    String XML_XPATH_SUPPLIER_NUMBER = "//infoTributaria/ruc";
    String XML_XPATH_ACCESS_KEY = "//infoTributaria/claveAcceso";
    String XML_XPATH_ESTABLISHMENT = "//infoTributaria/estab";
    String XML_XPATH_EMISSION_POINT = "//infoTributaria/ptoEmi";

}
