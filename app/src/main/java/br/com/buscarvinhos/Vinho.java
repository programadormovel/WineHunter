package br.com.buscarvinhos;

import java.io.Serializable;

public class Vinho implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //private variables
    long _id;
    String _descricao;
    String _vinicula;
    String _fornecedor;
    int _ano;
    double _volume;
    double _valor;
    String _caminho_foto;
    String _foto_texto;
    byte[] _foto;

    // Empty constructor
    public Vinho() {

    }

    // constructor
    public Vinho(long _id, String _descricao, String _vinicula,
                 String _fornecedor, int _ano, double _volume,
                 double _valor, String _caminho_foto, String _foto_texto,
                 byte[] _foto) {
        this._id = _id;
        this._descricao = _descricao;
        this._vinicula = _vinicula;
        this._fornecedor = _fornecedor;
        this._ano = _ano;
        this._volume = _volume;
        this._valor = _valor;
        this._caminho_foto = _caminho_foto;
        this._foto_texto = _foto_texto;
        this._foto = _foto;
    }

    // getting ID
    public long getID() {
        return this._id;
    }

    // setting id
    public void setID(long id) {
        this._id = id;
    }

    public String getDescricao() {
        return _descricao;
    }

    public void setDescricao(String _descricao) {
        this._descricao = _descricao;
    }

    public String getVinicula() {
        return _vinicula;
    }

    public void setVinicula(String _vinicula) {
        this._vinicula = _vinicula;
    }

    public String getFornecedor() {
        return _fornecedor;
    }

    public void setFornecedor(String _fornecedor) {
        this._fornecedor = _fornecedor;
    }

    public int getAno() {
        return _ano;
    }

    public void setAno(int _ano) {
        this._ano = _ano;
    }

    public double getVolume() {
        return _volume;
    }

    public void setVolume(double _volume) {
        this._volume = _volume;
    }

    public double getValor() {
        return _valor;
    }

    public void setValor(Double _valor) {
        this._valor = _valor;
    }

    public byte[] get_foto() {
        return _foto;
    }

    public void set_foto(byte[] foto) {
        this._foto = foto;
    }

    public String get_caminho_foto() {
        return _caminho_foto;
    }

    public void set_caminho_foto(String _caminho_foto) {
        this._caminho_foto = _caminho_foto;
    }

    public String get_foto_texto() {
        return _foto_texto;
    }

    public void set_foto_texto(String _foto_texto) {
        this._foto_texto = _foto_texto;
    }
}
