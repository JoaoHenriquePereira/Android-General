/**
 * Created by joaopereira on 03/10/14.
 */
 
# t_page will contain our page references
CREATE TABLE t_page (
    p_id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name` varchar(30) DEFAULT NULL COMMENT 'optional name',
    `host` varchar(30) NOT NULL COMMENT 'key host name',
    `url` varchar(200) NOT NULL COMMENT 'key url',
    first_visit TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_update TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_visit TIMESTAMP NOT NULL,
    n_visit INT(10) UNSIGNED DEFAULT 0,
    priority TINYINT(1) UNSIGNED NOT NULL COMMENT 'aka page rank',
    PRIMARY KEY (p_id),
    UNIQUE KEY (host,url)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='t for (semi-)transactional for new pages crawled';

# t_pagelink will contain pages referenced by others (links)
CREATE TABLE t_pagelink (
    pl_id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    p_id INT(10) UNSIGNED NOT NULL COMMENT 'p_id points to l_id',
    l_id INT(10) UNSIGNED NOT NULL COMMENT 'l_id references by p_id',
    PRIMARY KEY (pl_id),
    FOREIGN KEY (p_id) REFERENCES t_page (p_id),
    FOREIGN KEY (l_id) REFERENCES t_page (p_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='t for (semi-)transactional pages referenced by others';

#t_pagebank will contain our todo pages
CREATE TABLE t_pagebank (
    pb_id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    url VARCHAR(200) NOT NULL,
    priority TINYINT(1) UNSIGNED NOT NULL COMMENT 'aka page rank',
    PRIMARY KEY (pb_id),
    UNIQUE KEY (url)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='t for (semi-)transactional for queuing new pages to start crawling';

#s_page purpose is to archive pages blacklisted or simply gone
CREATE TABLE `s_page` (
  `p_id` int(10) unsigned NOT NULL,
  `name` varchar(30) DEFAULT NULL COMMENT 'optional name',
  `host` varchar(30) NOT NULL COMMENT 'key host name',
  `url` varchar(200) NOT NULL COMMENT 'key url',
  `first_visit` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_update` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `last_visit` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `n_visit` int(10) unsigned DEFAULT '0',
  `priority` tinyint(1) unsigned NOT NULL COMMENT 'aka page rank',
  PRIMARY KEY (`p_id`),
  UNIQUE KEY `host` (`host`,`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='s for storage table to be moved to other server in the future';

# s_pagelink will contain pages referenced by others (links)
CREATE TABLE s_pagelink (
    pl_id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    p_id INT(10) UNSIGNED NOT NULL COMMENT 'p_id points to l_id',
    l_id INT(10) UNSIGNED NOT NULL COMMENT 'l_id references by p_id',
    PRIMARY KEY (pl_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='s for storage table to be moved to other server in the future';


#t_batchlog purpose is to store batch run logs
CREATE TABLE `t_batchlog` (
  `bl_id` int(10) unsigned NOT NULL,
  `object` varchar(30) DEFAULT NULL COMMENT 'name of the object / method that ran',
  `status` tinyint(1) unsigned NOT NULL COMMENT 'batch status',
  `insert_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`bl_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='t for (semi-)transactional for storing batch run logs';

INSERT INTO `t_pagebank` (`pb_id`, `url`, `priority`)
VALUES
	(1, 'http://www.mit.edu/', 1);
