package com.greendelta.collaboration.model.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.greendelta.collaboration.model.AbstractEntity;

@Entity
@Table(name = "settings")
public class Setting<T extends SettingKey> extends AbstractEntity {

	@Id
	@Column(name = "id")
	private long id;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private SettingType type;

	@Column(name = "owner")
	@Enumerated(EnumType.STRING)
	public String owner;

	@Column(name = "name")
	@Enumerated(EnumType.STRING)
	public T name;

	@Column(name = "value")
	public String value;

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public static <T extends SettingKey> Setting<T> create(SettingType type, T key, String owner) {
		Setting<T> setting = new Setting<T>();
		setting.name = key;
		setting.type = type;
		setting.owner = owner;
		return setting;
	}

}
