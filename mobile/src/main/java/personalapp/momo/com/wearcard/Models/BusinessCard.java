package personalapp.momo.com.wearcard.Models;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by YassIne on 14/11/2015.
 */

/*
* Questa classe rappresenta il business card
* */
public class BusinessCard implements Serializable {
    private String ID;
    private String mNome;
    private String mCognome;
    private String mEmail;
    private String mNumero;
    private Bitmap mThumbnail;
    private String mOccupazione;


    public BusinessCard(){
        mNome = null;
        mCognome = null;
        mEmail = null;
        mNumero = null;
        mThumbnail = null;
        mOccupazione = null;

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    @Override
    public String toString() {
        return "BusinessCard{" +
                "ID='" + ID + '\'' +
                ", mNome='" + mNome + '\'' +
                ", mCognome='" + mCognome + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mNumero='" + mNumero + '\'' +
                ", mThumbnail=" + mThumbnail +
                ", mOccupazione='" + mOccupazione + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if( o instanceof BusinessCard){
            return this.getID() == ((BusinessCard)o).getID();
        }
        else return super.equals(o);

    }

    @Override
    public int hashCode() {
        int result = getID() != null ? getID().hashCode() : 0;
        result = 31 * result + (mNome != null ? mNome.hashCode() : 0);
        result = 31 * result + (mCognome != null ? mCognome.hashCode() : 0);
        result = 31 * result + (mEmail != null ? mEmail.hashCode() : 0);
        result = 31 * result + (mNumero != null ? mNumero.hashCode() : 0);
        result = 31 * result + (mThumbnail != null ? mThumbnail.hashCode() : 0);
        result = 31 * result + (getmOccupazione() != null ? getmOccupazione().hashCode() : 0);
        return result;
    }
}
