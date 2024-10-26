package amehry.ultimateads.interfaces;

public interface DataFetchListener {

    void onDataFetched();

    /**
     * This method should perform a specific action.
     *
     *FIXME :  Error Codes :
      * 1 : user country is blocked 400
      * 2 : country code is null 300;
      * 3 : error in fetching data 100;
      * 3 : internet error 50;

     */
    void onDataFetchError(String error);
}
