package personalapp.momo.com.wearcard.Models;

import android.graphics.Bitmap;

/**
 * Created by YassIne on 14/11/2015.
 */

/*
* Questa classe rappresenta il business card
* */
public class BusinessCard {
    private String mNome;
    private String mCognome;
    private String mEmail;
    private String mNumero;
    private Bitmap mThumbnail;
    private String mOccupazione;


    public BusinessCard(){

    }

    public String getNome() {
        return mNome;
    }

    public void setNome(String mName) {
        this.mNome = mName;
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

    public Bitmap getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(Bitmap mThumbnail) {
        this.mThumbnail = mThumbnail;
    }

    public String getmOccupazione() {
        return mOccupazione;
    }

    public void setmOccupazione(String mOccupazione) {
        this.mOccupazione = mOccupazione;
    }
}
