CREATE TABLE `to`.o_vats (
  id int(11) NOT NULL AUTO_INCREMENT,
  id_blank_ts_info int(11) NOT NULL COMMENT 'ссылка на бланк, по которому выдана с/ф',
  unp int(11) NOT NULL COMMENT 'УНП организации на момент выдачи с/ф по НДС (является частью номера С/Ф)',
  year smallint(6) NOT NULL COMMENT 'год выдачи с/ф по НДС (является частью номера С/Ф)',
  number bigint(20) NOT NULL COMMENT 'Присвоенный номер с/ф',
  PRIMARY KEY (id)
)
ENGINE = INNODB
AUTO_INCREMENT = 1
CHARACTER SET utf8
COLLATE utf8_general_ci;




CREATE TABLE `to`.o_vat_settings (
  year smallint(6) NOT NULL,
  begin bigint(20) UNSIGNED NOT NULL,
  end bigint(20) UNSIGNED NOT NULL,
  PRIMARY KEY (year),
  UNIQUE INDEX UK_o_vat_settings_year (year)
)
ENGINE = INNODB
AVG_ROW_LENGTH = 8192
CHARACTER SET utf8
COLLATE utf8_general_ci;




SELECT 
  vats.unp v_unp, vats.year v_year, vats.number v_number,
  bti.date_ot date1,
  oi.name, oi.unp,
  stti.summa_no_tax withoutVAT,
  stti.summa_oplaty withVAT,
  stti.summa_oplaty - stti.summa_no_tax VAT
  FROM o_vats vats RIGHT JOIN  blanc_ts_info bti ON bti.id_blanc_ts_info = vats.id_blank_ts_info
  INNER JOIN ts_info ti ON bti.id_ts_info = ti.id_ts_info

  INNER JOIN owner_info oi ON ti.id_owner_sobs = oi.id_owner
  INNER JOIN sd_tarifs_ts_info stti ON ti.id_ts_info = stti.id_ts_info

  WHERE oi.id_owner_type = 2