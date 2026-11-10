package br.com.assistencia.model;

public class Equipamento {
    private int id;
    private int categoriaId;
    private String categoriaNome;
    private String marca;
    private String modelo;
    private String numeroSerie;

    public Equipamento() { }
    public Equipamento(int id, int categoriaId, String marca, String modelo, String numeroSerie) {
        this.id = id;
        this.categoriaId = categoriaId;
        this.marca = marca;
        this.modelo = modelo;
        this.numeroSerie = numeroSerie;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }
    public String getCategoriaNome() { return categoriaNome; }
    public void setCategoriaNome(String categoriaNome) { this.categoriaNome = categoriaNome; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
}
