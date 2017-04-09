CREATE TABLE `notifications` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `email` text NOT NULL,
  `notification_time` varchar(11) NOT NULL DEFAULT '',
  `location_start` mediumtext NOT NULL,
  `location_start_lat` float(10,6) NOT NULL,
  `location_start_lng` float(10,6) NOT NULL,
  `location_end` mediumtext NOT NULL,
  `location_end_lat` float(10,6) NOT NULL,
  `location_end_lng` float(10,6) NOT NULL,
  `confirmed` tinyint(1) NOT NULL DEFAULT '0',
  `hash` tinytext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=latin1;