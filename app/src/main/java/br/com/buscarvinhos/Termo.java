package br.com.buscarvinhos;

import java.io.Serializable;
import java.util.List;

public class Termo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //private variables
    int _id;
    String _descricao;
    List<Termo> termos;

    // Empty constructor
    public Termo() {

    }

    // constructor
    public Termo(int _id, String _descricao) {
        this._id = _id;
        this._descricao = _descricao;
    }

    // getting ID
    public int getID() {
        return this._id;
    }

    // setting id
    public void setID(int id) {
        this._id = id;
    }

    public String getDescricao() {
        return _descricao;
    }

    public void setDescricao(String _descricao) {
        this._descricao = _descricao;
    }

    public List<Termo> getTermos() {
        return termos;
    }

    public void setTermos(Termo termo) {
        termos.add(termo);
    }
}
