package com.welab.service3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoqian.wen
 * @create 2016-12-27 9:46
 **/
@Service
public class ZipkinService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String,String> getTrace(String traceId){
        String sql = "select * from zipkin_spans where parent_id = '"+traceId+"'";
        Map<String, String> map = jdbcTemplate.query(sql, new RowMapper<Map<String,String>>() {
            public Map<String, String> mapRow(ResultSet rss, int i) throws SQLException {
                Map<String, String> value = new HashMap<String, String>();
                value.put("trace_id", rss.getString("trace_id"));
                value.put("id", rss.getString("id"));
                value.put("name", rss.getString("name"));
                value.put("parent_id", rss.getString("parent_id"));
                value.put("start_ts", rss.getString("start_ts"));
                value.put("duration", rss.getString("duration"));
                return value;
            }
        }).get(0);
        return map;
    }
}
