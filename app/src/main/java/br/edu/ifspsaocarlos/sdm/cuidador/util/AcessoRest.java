package br.edu.ifspsaocarlos.sdm.cuidador.util;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Classe que irá auxiliar na comunicação com o servidor RESTFul enviando e obtendo dados.
 *
 * @author Anderson Canale Garcia
 * @author Lucas Petto
 */
public final class AcessoRest {

    private AcessoRest(){

    }

    private static final String BASE_URL = "http://www.nobile.pro.br/sdm/mensageiro/";

    private static AsyncHttpClient cliente = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        cliente.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        cliente.put(getAbsoluteUrl(url),params,responseHandler);
    }

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        cliente.get(getAbsoluteUrl(url), responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        cliente.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}

