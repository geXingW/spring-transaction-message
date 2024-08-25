package com.gexingw.spring.transaction.message.infrastructure.repository;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gexingw.spring.transaction.message.message.MessageStatus;
import com.gexingw.spring.transaction.message.message.TransactionMessage;
import com.gexingw.spring.transaction.message.message.TransactionMessageRepository;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * @author GeXingW
 */
public class JdbcTransactionMessageRepositoryImpl implements TransactionMessageRepository {

    private final JdbcOperations jdbcOperations;

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    public JdbcTransactionMessageRepositoryImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<TransactionMessage> queryAllRetryable(long currentTimestamp) {
        String querySql = "select * from transaction_message where next_retry_time <= ? and message_status = ?";
        Object[] args = {currentTimestamp, MessageStatus.NORMAL.toString()};
        int[] argTypes = {Types.INTEGER, Types.CHAR};
        return jdbcOperations.query(querySql, args, argTypes, new TransactionMessageRowMapper());
    }

    @Override
    public void save(TransactionMessage message) {
        if (message.getId() == null) {
            this.create(message);
            return;
        }

        this.update(message);
    }

    private void create(TransactionMessage message) {
        long id = System.currentTimeMillis();
        String sql = "INSERT INTO transaction_message (id, topic, `key`, max_retry_count, next_retry_time, payload, message_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String payloadJsonStr = this.toJsonString(message.getPayload());
        Object[] args = {
                id, message.getTopic(), message.getKey(), message.getMaxRetryCount(), message.getNextRetryTime(), payloadJsonStr
                , message.getMessageStatus().toString()
        };
        if (jdbcOperations.update(sql, args) <= 0) {
            throw new RuntimeException("保存事务消息失败");
        }

        message.setId(id);
    }

    private void update(TransactionMessage message) {
        String sql = "UPDATE transaction_message set retried_count = ?, next_retry_time = ?, message_status = ? WHERE id = ?";
        Object[] args = {message.getRetriedCount(), message.getNextRetryTime(), message.getMessageStatus().toString(), message.getId()};
        if (jdbcOperations.update(sql, args) <= 0) {
            throw new RuntimeException("保存事务消息失败");
        }
    }

    @Override
    public void remove(Serializable id) {
        if (jdbcOperations.update("DELETE FROM transaction_message WHERE id = ?", id) <= 0) {
            throw new RuntimeException("删除事务消息失败");
        }
    }

    @Override
    public TransactionMessage find(Serializable id) {
        Object[] args = {id};
        int[] argTypes = {Types.BIGINT};
        String query = "SELECT * FROM transaction_message WHERE id = ?";
        List<TransactionMessage> queryResult = jdbcOperations.query(query, args, argTypes, new TransactionMessageRowMapper());

        return !queryResult.isEmpty() ? queryResult.get(0) : null;
    }

    public static class TransactionMessageRowMapper implements RowMapper<TransactionMessage> {

        @Override
        public TransactionMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TransactionMessage(
                    rs.getLong("id")
                    , rs.getString("topic")
                    , rs.getString("key")
                    , rs.getInt("max_retry_count")
                    , rs.getInt("retried_count")
                    , rs.getLong("next_retry_time")
                    , rs.getObject("payload")
                    , MessageStatus.valueOf(rs.getString("message_status"))
            );
        }
    }

    private String toJsonString(Object data) {
        try {
            return OBJECT_MAPPER.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

}
