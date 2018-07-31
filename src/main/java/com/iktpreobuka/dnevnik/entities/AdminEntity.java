package com.iktpreobuka.dnevnik.entities;

import javax.persistence.Entity;

@Entity
public class AdminEntity extends UserEntity{
	public AdminEntity() {}
	public AdminEntity(AdminEntity admin) {
		this.id=admin.getId();
		this.firstName=admin.getFirstName();
		this.lastName=admin.getLastName();
		this.username=admin.getUsername();
		this.password=admin.getPassword();
		this.userRole=admin.getUserRole();
		this.version=admin.getVersion();
	}

}
