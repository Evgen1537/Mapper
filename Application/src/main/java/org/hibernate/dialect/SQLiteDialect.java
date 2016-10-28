package org.hibernate.dialect;

import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.type.*;

import java.sql.Types;

/**
 * Project: Driver Pay Enhanced
 * Author:  Evgeniy
 * Created: 26-05-2016 23:34
 */
public class SQLiteDialect extends Dialect {
	public SQLiteDialect() {
		registerColumnType(Types.NULL, LongType.INSTANCE.getName());
		registerColumnType(Types.INTEGER, LongType.INSTANCE.getName());
		registerColumnType(Types.FLOAT, DoubleType.INSTANCE.getName());
		registerColumnType(Types.VARCHAR, StringType.INSTANCE.getName());
		registerColumnType(Types.BLOB, BlobType.INSTANCE.getName());

		registerHibernateType(Types.NULL, LongType.INSTANCE.getName());
		registerHibernateType(Types.INTEGER, LongType.INSTANCE.getName());
		registerHibernateType(Types.FLOAT, DoubleType.INSTANCE.getName());
		registerHibernateType(Types.VARCHAR, StringType.INSTANCE.getName());
		registerHibernateType(Types.BLOB, BlobType.INSTANCE.getName());

		//Types.NUMERIC;
		//Types.DECIMAL;
		//Types.CHAR;
		//Types.VARCHAR;
		//Types.LONGVARCHAR;
		//Types.DATE;
		//Types.TIME;
		//Types.TIMESTAMP;
		//Types.BINARY;
		//Types.VARBINARY;
		//Types.LONGVARBINARY;
		//Types.NULL;
		//Types.OTHER;
		//Types.JAVA_OBJECT;
		//Types.DISTINCT;
		//Types.STRUCT;
		//Types.ARRAY;
		//Types.BLOB;
		//Types.CLOB;
		//Types.REF;
		//Types.DATALINK;
		//Types.BOOLEAN;
		//Types.ROWID;
		//Types.NCHAR;
		//Types.NVARCHAR;
		//Types.LONGNVARCHAR;
		//Types.NCLOB;
		//Types.SQLXML;
		//Types.REF_CURSOR;
		//Types.TIME_WITH_TIMEZONE;
		//Types.TIMESTAMP_WITH_TIMEZONE;

		registerFunction( "concat", new VarArgsSQLFunction(StringType.INSTANCE, "", "||", "") );
		registerFunction( "mod", new SQLFunctionTemplate( IntegerType.INSTANCE, "?1 % ?2" ) );
		registerFunction( "substr", new StandardSQLFunction("substr", StringType.INSTANCE) );
		registerFunction( "substring", new StandardSQLFunction( "substr", StringType.INSTANCE ) );
	}

	public boolean supportsIdentityColumns() {
		return true;
	}

	public boolean hasDataTypeInIdentityColumn() {
		return false; // As specify in NHibernate dialect
	}

	public String getIdentityColumnString() {
		return "integer";
	}

	public String getIdentitySelectString() {
		return "select last_insert_rowid()";
	}

	public boolean supportsLimit() {
		return true;
	}

	protected String getLimitString(String query, boolean hasOffset) {
		return new StringBuffer(query.length()+20).
				append(query).
				append(hasOffset ? " limit ? offset ?" : " limit ?").
				toString();
	}

	public boolean supportsTemporaryTables() {
		return true;
	}

	public String getCreateTemporaryTableString() {
		return "create temporary table if not exists";
	}

	public boolean dropTemporaryTableAfterUse() {
		return false;
	}

	public boolean supportsCurrentTimestampSelection() {
		return true;
	}

	public boolean isCurrentTimestampSelectStringCallable() {
		return false;
	}

	public String getCurrentTimestampSelectString() {
		return "select current_timestamp";
	}

	public boolean supportsUnionAll() {
		return true;
	}

	public boolean hasAlterTable() {
		return false; // As specify in NHibernate dialect
	}

	public boolean dropConstraints() {
		return false;
	}

	public String getAddColumnString() {
		return "add column";
	}

	public String getForUpdateString() {
		return "";
	}

	public boolean supportsOuterJoinForUpdate() {
		return false;
	}

	public String getDropForeignKeyString() {
		throw new UnsupportedOperationException("No drop foreign key syntax supported by SQLiteDialect");
	}

	public String getAddForeignKeyConstraintString(String constraintName,
												   String[] foreignKey, String referencedTable, String[] primaryKey,
												   boolean referencesPrimaryKey) {
		throw new UnsupportedOperationException("No add foreign key syntax supported by SQLiteDialect");
	}

	public String getAddPrimaryKeyConstraintString(String constraintName) {
		throw new UnsupportedOperationException("No add primary key syntax supported by SQLiteDialect");
	}

	public boolean supportsIfExistsBeforeTableName() {
		return true;
	}

	public boolean supportsCascadeDelete() {
		return false;
	}

	@Override
	public boolean bindLimitParametersInReverseOrder() {
		return true;
	}
}
