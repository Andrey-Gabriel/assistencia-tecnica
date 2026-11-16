package br.com.assistencia.entidade;

import br.com.assistencia.controle.Equipamento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ManterEquipamento {

    public List<Equipamento> listar() throws SQLException {
        Sessao.validarAcesso();
        List<Equipamento> lista = new ArrayList<>();
        String sql = "SELECT e.*, c.nome AS categoria_nome FROM equipamento e "
                + "LEFT JOIN categoria_equipamento c ON c.id = e.categoria_id ORDER BY e.marca, e.modelo, e.id";
        try (Connection c = DAO.abrirBanco(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            while (r.next()) {
                Equipamento equipamento = new Equipamento(r.getInt("id"), r.getInt("categoria_id"),
                        r.getString("marca"), r.getString("modelo"), r.getString("numero_serie"));
                equipamento.setCategoriaNome(r.getString("categoria_nome"));
                lista.add(equipamento);
            }
        }
        return lista;
    }

    public void inserir(Equipamento equipamento) throws SQLException {
        Sessao.validarAcesso();
        if (equipamento == null || equipamento.getId() != 0) {
            throw new IllegalArgumentException("Equipamento inválido.");
        }
        validar(equipamento);
        String sql = "INSERT INTO equipamento (categoria_id, marca, modelo, numero_serie) VALUES (?, ?, ?, ?)";
        try (Connection c = DAO.abrirBanco(); PreparedStatement p = c.prepareStatement(sql)) {
            preencher(p, equipamento);
            p.executeUpdate();
        }
    }

    public void alterar(Equipamento equipamento) throws SQLException {
        Sessao.validarAcesso();
        if (equipamento == null || equipamento.getId() <= 0) {
            throw new IllegalArgumentException("Selecione um equipamento válido.");
        }
        validar(equipamento);
        String sql = "UPDATE equipamento SET categoria_id = ?, marca = ?, modelo = ?, numero_serie = ? WHERE id = ?";
        try (Connection c = DAO.abrirBanco(); PreparedStatement p = c.prepareStatement(sql)) {
            preencher(p, equipamento);
            p.setInt(5, equipamento.getId());
            if (p.executeUpdate() == 0) {
                throw new SQLException("Equipamento não encontrado. Atualize a lista.");
            }
        }
    }

    public void excluir(int id) throws SQLException {
        Sessao.validarAcesso();
        if (id <= 0) {
            throw new IllegalArgumentException("Selecione um equipamento válido.");
        }
        try (Connection c = DAO.abrirBanco(); PreparedStatement p = c.prepareStatement("DELETE FROM equipamento WHERE id=?")) {
            p.setInt(1, id);
            if (p.executeUpdate() == 0) {
                throw new SQLException("Equipamento não encontrado. Atualize a lista.");
            }
        }
    }

    private void validar(Equipamento equipamento) {
        if (equipamento.getCategoriaId() < 0) {
            throw new IllegalArgumentException("Categoria inválida.");
        }
        String marca = equipamento.getMarca() == null ? "" : equipamento.getMarca().trim();
        if (marca.length() > 80) {
            throw new IllegalArgumentException("A marca deve ter até 80 caracteres.");
        }
        String modelo = equipamento.getModelo();
        if (modelo == null || modelo.isBlank() || modelo.trim().length() > 120) {
            throw new IllegalArgumentException("Preencha a informação de modelo (até 120 caracteres).");
        }
        String serie = equipamento.getNumeroSerie() == null ? "" : equipamento.getNumeroSerie().trim();
        if (serie.length() > 100) {
            throw new IllegalArgumentException("O número de série deve ter até 100 caracteres.");
        }
        equipamento.setMarca(marca.isEmpty() ? null : marca);
        equipamento.setModelo(modelo.trim());
        equipamento.setNumeroSerie(serie.isEmpty() ? null : serie);
    }

    private void preencher(PreparedStatement comando, Equipamento equipamento) throws SQLException {
        if (equipamento.getCategoriaId() == 0) {
            comando.setNull(1, Types.INTEGER);
        } else {
            comando.setInt(1, equipamento.getCategoriaId());
        }
        comando.setString(2, equipamento.getMarca());
        comando.setString(3, equipamento.getModelo());
        comando.setString(4, equipamento.getNumeroSerie());
    }
}
