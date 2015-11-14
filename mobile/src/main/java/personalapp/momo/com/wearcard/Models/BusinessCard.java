package personalapp.momo.com.wearcard.Models;

/**
 * Created by YassIne on 14/11/2015.
 */

/*
* Questa classe rappresenta il business card
* */
public class BusinessCard {
    private String mName;
    private String mCognome;
    private String mEmail;
    private String mNumero;


    public BusinessCard(){

    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getCognome() {
        return mCognome;
    }

    public void setCognome(String mCognome) {
        this.mCognome = mCognome;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getNumero() {
        return mNumero;
    }

    public void setmNumero(String mNumero) {
        this.mNumero = mNumero;
    }
}
