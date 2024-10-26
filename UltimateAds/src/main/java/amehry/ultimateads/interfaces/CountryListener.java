package amehry.ultimateads.interfaces;

public interface CountryListener {
    void onCountryReceived(boolean blocked);
    void onCountryError(String error);
}
