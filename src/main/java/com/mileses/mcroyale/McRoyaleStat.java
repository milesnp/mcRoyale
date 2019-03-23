package com.mileses.mcroyale;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;

@Entity
@Table(name = "mcr_stat")
public class McRoyaleStat {

	@Id
	private int id;

	@NotNull
	private String playerName;

	@NotEmpty
	private String stat;

	@NotNull
	private int value;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getStat() {
		return stat;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
