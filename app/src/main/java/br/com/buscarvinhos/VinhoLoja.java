package br.com.buscarvinhos;

import java.io.Serializable;

public class VinhoLoja implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public String BtTitulo;
    //private variables
    long _id;
    String _descricao;
    double _valor;
    String _loja;
    String _link;
    String _imagem;

    // Empty constructor
    public VinhoLoja() {

    }

    // constructor
    public VinhoLoja(long _id, String _descricao, String _loja,
                     String _link, double _valor, String _imagem) {
        this._id = _id;
        this._descricao = _descricao;
        this._loja = _loja;
        this._link = _link;
        this._valor = _valor;
        this._imagem = _imagem;
    }

    public String get_imagem() {
        return _imagem;
    }

    public void set_imagem(String _imagem) {
        this._imagem = _imagem;
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

    public double getValor() {
        return _valor;
    }

    public void setValor(Double _valor) {
        this._valor = _valor;
    }

    public String getLink() {
        return _link;
    }

    public void setLink(String _link) {
        this._link = _link;
    }

}
