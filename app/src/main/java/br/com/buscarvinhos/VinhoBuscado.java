package br.com.buscarvinhos;

import java.io.Serializable;

public class VinhoBuscado implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //private variables
    long _id;
    String _descricao;
    String _imagemVinho;
    String _ivBuscarLojas;

    // Empty constructor
    public VinhoBuscado() {

    }

    // constructor
    public VinhoBuscado(long _id, String _descricao, String _imagemVinho) {
        //double _valor){
        this._id = _id;
        this._descricao = _descricao;
        this._imagemVinho = _imagemVinho;
        //this._valor = _valor;
    }


    //double _valor;

    public String get_imagemVinho() {
        return _imagemVinho;
    }

    public void set_imagemVinho(String _imagemVinho) {
        this._imagemVinho = _imagemVinho;
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

	/*public double getValor() {
		return _valor;
	}

	public void setValor(Double _valor) {
		this._valor = _valor;
	}*/
}
