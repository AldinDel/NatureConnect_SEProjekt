package at.fhv.Event.config;

import at.fhv.Event.domain.model.Difficulty;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;

public class PostgreSQLEnumType implements UserType<Difficulty> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<Difficulty> returnedClass() {
        return Difficulty.class;
    }

    @Override
    public boolean equals(Difficulty x, Difficulty y) {
        return x == y;
    }

    @Override
    public int hashCode(Difficulty x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public Difficulty nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String value = rs.getString(position);
        if (value == null) {
            return null;
        }
        return Difficulty.valueOf(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Difficulty value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value.name(), Types.OTHER);
        }
    }

    @Override
    public Difficulty deepCopy(Difficulty value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Difficulty value) {
        return value;
    }

    @Override
    public Difficulty assemble(Serializable cached, Object owner) {
        return (Difficulty) cached;
    }
}