package br.com.assistencia.dao;

import br.com.assistencia.model.Equipamento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipamentoDAO {
    public List<Equipamento> listar() throws SQLException {
        List<Equipamento> lista = new ArrayList<>();
        String sql = "SELECT e.*, c.nome AS categoria_nome FROM equipamento e "
                + "JOIN categoria_equipamento c ON c.id = e.categoria_id ORDER BY e.marca, e.modelo, e.id";
        try (Connection c = conexao.conectar(); PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                Equipamento e = new Equipamento(r.getInt("id"), r.getInt("categoria_id"),
                        r.getString("marca"), r.getString("modelo"), r.getString("numero_serie"));
                e.setCategoriaNome(r.getString("categoria_nome"));
                lista.add(e);
            }
        }
        return lista;
    }

    public void salvar(Equipamento e) throws SQLException {
        boolean novo = e.getId() == 0;
        String sql = novo ? "INSERT INTO equipamento (categoria_id,marca,modelo,numero_serie) VALUES (?,?,?,?)"
                : "UPDATE equipamento SET categoria_id=?,marca=?,modelo=?,numero_serie=? WHERE id=?";
        try (Connection c = conexao.conectar(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, e.getCategoriaId()); p.setString(2, e.getMarca()); p.setString(3, e.getModelo());
            p.setString(4, e.getNumeroSerie());
            if (!novo) p.setInt(5, e.getId());
            if (p.executeUpdate() == 0) throw new SQLException("Equipamento não encontrado. Atualize a lista.");
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection c = conexao.conectar();
             PreparedStatement p = c.prepareStatement("DELETE FROM equipamento WHERE id=?")) {
            p.setInt(1, id);
            if (p.executeUpdate() == 0) throw new SQLException("Equipamento não encontrado. Atualize a lista.");
        }
    }
}
