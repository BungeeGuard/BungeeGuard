-- phpMyAdmin SQL Dump
-- version 3.4.11.1deb2+deb7u1
-- http://www.phpmyadmin.net
--
-- Client: localhost
-- Généré le: Mer 08 Avril 2015 à 14:58
-- Version du serveur: 1.0.17
-- Version de PHP: 5.4.39-0+deb7u2

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données: `minecraft_servers`
--

-- --------------------------------------------------------
-- Structure de la table `bungeelitycs`
--

CREATE TABLE IF NOT EXISTS `bungeelitycs` (
  `id` int(32) unsigned NOT NULL AUTO_INCREMENT,
  `server_id` varchar(11) NOT NULL DEFAULT 'hub',
  `joined_at` datetime NOT NULL,
  `leaved_at` datetime DEFAULT NULL,
  `uuid` binary(16) NOT NULL DEFAULT '\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0',
  PRIMARY KEY (`id`),
  KEY `uuid` (`uuid`) USING BTREE
) ENGINE=TokuDB DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `bungee_ban`
--

CREATE TABLE IF NOT EXISTS `bungee_ban` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL DEFAULT '1',
  `nameBanned` varchar(255) NOT NULL DEFAULT ' ',
  `uuidBanned` varchar(255) NOT NULL DEFAULT ' ',
  `nameAdmin` varchar(255) NOT NULL DEFAULT ' ',
  `uuidAdmin` varchar(255) NOT NULL DEFAULT ' ',
  `ban` bigint(20) NOT NULL DEFAULT '0',
  `reason` text NOT NULL,
  `unban` bigint(20) DEFAULT NULL,
  `unbanReason` text,
  `unbanName` varchar(255) DEFAULT ' ',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ip` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=4961 ;

--
--
-- Structure de la table `bungee_blocked_commands`
--

CREATE TABLE IF NOT EXISTS `bungee_blocked_commands` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `command` varchar(42) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=45 ;

--
-- Contenu de la table `bungee_blocked_commands`
--

INSERT INTO `bungee_blocked_commands` (`id`, `command`) VALUES(1, '/pl*');
INSERT INTO `bungee_blocked_commands` (`id`, `command`) VALUES(2, '/bukkit:*');
INSERT INTO `bungee_blocked_commands` (`id`, `command`) VALUES(3, '/about*');
INSERT INTO `bungee_blocked_commands` (`id`, `command`) VALUES(4, '/bungeecord*');
INSERT INTO `bungee_blocked_commands` (`id`, `command`) VALUES(5, '/version*');
INSERT INTO `bungee_blocked_commands` (`id`, `command`) VALUES(6, '/me*');
INSERT INTO `bungee_blocked_commands` (`id`, `command`) VALUES(8, '/reload');
--
-- Structure de la table `bungee_broadcasts`
--

CREATE TABLE IF NOT EXISTS `bungee_broadcasts` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `message` varchar(500) DEFAULT NULL,
  `servers` varchar(255) DEFAULT NULL COMMENT 'Liste de serveurs séparés par ":"',
  PRIMARY KEY (`id`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=11 ;

--
-- Contenu de la table `bungee_broadcasts`
--

INSERT INTO `bungee_broadcasts` (`id`, `message`, `servers`) VALUES(1, '&7Pour générer un monde vide : /mv create name NORMAL -g VoidGenerator', 'build*');
INSERT INTO `bungee_broadcasts` (`id`, `message`, `servers`) VALUES(2, '&6Nos &bKTP &6sont fièrement propulsés par &bOMGServ &6!', 'ktp*');
--
-- Structure de la table `bungee_cheats`
--

CREATE TABLE IF NOT EXISTS `bungee_cheats` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `player_name` varchar(16) NOT NULL DEFAULT '',
  `cheat` varchar(3) NOT NULL DEFAULT '',
  `score` double NOT NULL,
  `server_name` varchar(16) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pseudo` (`player_name`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=2120638 ;

--
-- Contenu de la table `bungee_cheats`
--
INSERT INTO `bungee_cheats` (`id`, `player_name`, `cheat`, `score`, `server_name`, `created_at`) VALUES(557, 'Selken964', 'ff', 2, 'fof5', '2014-10-13 16:09:09');
--
-- Structure de la table `bungee_cmd`
--

CREATE TABLE IF NOT EXISTS `bungee_cmd` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `action` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=458 ;

-- --------------------------------------------------------

--
-- Structure de la table `bungee_config`
--

CREATE TABLE IF NOT EXISTS `bungee_config` (
  `id` int(1) NOT NULL AUTO_INCREMENT,
  `max_players` int(3) NOT NULL,
  `motd` tinytext NOT NULL,
  `broadcast_delay` int(11) DEFAULT '180' COMMENT 'Duree en secondes',
  PRIMARY KEY (`id`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=4 ;

--
-- Contenu de la table `bungee_config`
--

INSERT INTO `bungee_config` (`id`, `max_players`, `motd`, `broadcast_delay`) VALUES(1, 700, '&6Mon petit poney', 180);

-- --------------------------------------------------------

--
-- Structure de la table `bungee_forced_host`
--

CREATE TABLE IF NOT EXISTS `bungee_forced_host` (
  `ip` varchar(40) NOT NULL DEFAULT '',
  `to_server` varchar(40) NOT NULL DEFAULT ''
) ENGINE=TokuDB DEFAULT CHARSET=utf8 `compression`='tokudb_zlib';

--
-- Contenu de la table `bungee_forced_host`
--

INSERT INTO `bungee_forced_host` (`ip`, `to_server`) VALUES('mc.votreServeur.com', 'hub');
--
-- Structure de la table `bungee_friends`
--

CREATE TABLE IF NOT EXISTS `bungee_friends` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uuid1` binary(16) NOT NULL DEFAULT '\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0',
  `uuid2` binary(16) NOT NULL DEFAULT '\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0',
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=55467 ;

--
-- Contenu de la table `bungee_friends`
--

INSERT INTO `bungee_friends` (`id`, `uuid1`, `uuid2`, `created_at`) VALUES(1, '�Ε�ԞF��"�!*��', 'k�Ĳ6M�pD�', '2014-12-27 17:51:04');

--
-- Structure de la table `bungee_friendvip`
--

CREATE TABLE IF NOT EXISTS `bungee_friendvip` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `sender` varchar(38) NOT NULL DEFAULT '',
  `recipient` varchar(38) NOT NULL DEFAULT '',
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=119 ;

--
-- Contenu de la table `bungee_friendvip`
--

INSERT INTO `bungee_friendvip` (`id`, `sender`, `recipient`, `created_at`) VALUES(37, '50a64f03-164a-4693-80bb-47e3e4bef5e8', '8c2c8340-93b9-4c78-b172-026da628f6a4', '2015-03-08 00:24:08');
--
-- Structure de la table `bungee_instances`
--

CREATE TABLE IF NOT EXISTS `bungee_instances` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `server_id` varchar(40) CHARACTER SET latin1 NOT NULL DEFAULT '',
  `bind_address` varchar(40) CHARACTER SET latin1 DEFAULT '0.0.0.0:25565',
  PRIMARY KEY (`id`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=8 ;

--
-- Contenu de la table `bungee_instances`
--

INSERT INTO `bungee_instances` (`id`, `server_id`, `bind_address`) VALUES(1, 'bungee1', '0.0.0.0:25565');
INSERT INTO `bungee_instances` (`id`, `server_id`, `bind_address`) VALUES(2, 'bungee2', '0.0.0.0:25565');
--
-- Structure de la table `bungee_mute`
--

CREATE TABLE IF NOT EXISTS `bungee_mute` (
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL DEFAULT '1',
  `nameMute` varchar(255) NOT NULL DEFAULT ' ',
  `uuidMute` varchar(255) NOT NULL DEFAULT ' ',
  `nameAdmin` varchar(255) NOT NULL DEFAULT ' ',
  `uuidAdmin` varchar(255) NOT NULL DEFAULT ' ',
  `mute` bigint(20) NOT NULL,
  `reason` text NOT NULL,
  `unmute` bigint(20) DEFAULT NULL,
  `unmuteReason` text,
  `unmuteName` varchar(255) DEFAULT ' ',
  PRIMARY KEY (`id`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=12244 ;

--
-- Contenu de la table `bungee_mute`
--
-- Structure de la table `bungee_premade_message`
--

CREATE TABLE IF NOT EXISTS `bungee_premade_message` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `slug` varchar(42) NOT NULL DEFAULT '',
  `text` varchar(300) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=17 ;

--
-- Contenu de la table `bungee_premade_message`
--

INSERT INTO `bungee_premade_message` (`id`, `slug`, `text`) VALUES(1, 'cheat', '§rLa §ctriche§r est interdite sur le serveur. Si vous pensez qu''il s''agit d''une erreur, merci de vous rendre sur le forum.');
INSERT INTO `bungee_premade_message` (`id`, `slug`, `text`) VALUES(2, 'test', '§bNous sommes navrés, mais cette action a été effectuée dans le cadre d''une expérience scientifique. Cordialement, §aL''administration. §6');
--
-- Structure de la table `bungee_servers`
--

CREATE TABLE IF NOT EXISTS `bungee_servers` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(40) DEFAULT NULL,
  `address` varchar(40) DEFAULT NULL,
  `pretty_name` varchar(60) DEFAULT NULL,
  `short_name` varchar(16) DEFAULT NULL,
  `restricted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `name` (`name`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=281 ;

--
-- Contenu de la table `bungee_servers`
--

INSERT INTO `bungee_servers` (`id`, `name`, `address`, `pretty_name`, `short_name`, `restricted`) VALUES(1, 'hub', '127.0.0.1:19000', 'Hub', 'Hub', 0);
INSERT INTO `bungee_servers` (`id`, `name`, `address`, `pretty_name`, `short_name`, `restricted`) VALUES(2, 'lobby1', '127.0.0.1:10001', 'Lobby 1', '&3Lobby 1', 0);
INSERT INTO `bungee_servers` (`id`, `name`, `address`, `pretty_name`, `short_name`, `restricted`) VALUES(3, 'lobby2', '127.0.0.1:10002', 'Lobby 2', '&4Lobby 2', 0);
--
-- Structure de la table `bungee_tokens`
--

CREATE TABLE IF NOT EXISTS `bungee_tokens` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `token` varchar(10) CHARACTER SET latin1 NOT NULL DEFAULT '',
  `action` varchar(100) CHARACTER SET latin1 NOT NULL DEFAULT '',
  `usages` int(10) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `valid_until` datetime DEFAULT NULL,
  `created_by` varchar(16) CHARACTER SET latin1 NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=56 ;

--
-- Contenu de la table `bungee_tokens`
--

INSERT INTO `bungee_tokens` (`id`, `token`, `action`, `usages`, `created_at`, `valid_until`, `created_by`) VALUES(1, '9Armp72i', 'vip:1mo', 1, '2015-02-22 17:57:23', '2015-02-24 17:57:23', 'PunKeel');
--
-- Structure de la table `bungee_token_uses`
--

CREATE TABLE IF NOT EXISTS `bungee_token_uses` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `token` varchar(20) NOT NULL DEFAULT '',
  `uuid` varchar(39) NOT NULL DEFAULT '',
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=111 ;

--
-- Contenu de la table `bungee_token_uses`
--

INSERT INTO `bungee_token_uses` (`id`, `token`, `uuid`, `created_at`) VALUES(1, '9Armp72i', '61d349a6-7cb2-4923-93c9-be2aaed7ebf4', '2015-02-23 11:15:19');

--
-- Structure de la table `bungee_welcome_title`
--

CREATE TABLE IF NOT EXISTS `bungee_welcome_title` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `message` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=8 ;

--
-- Contenu de la table `bungee_welcome_title`
--

INSERT INTO `bungee_welcome_title` (`id`, `message`) VALUES(1, '&6BungeeGuard powa!');
INSERT INTO `bungee_welcome_title` (`id`, `message`) VALUES(2, '&bJe vous aime :$!');
INSERT INTO `bungee_welcome_title` (`id`, `message`) VALUES(3, '&bMade with love!');

-- --------------------------------------------------------
--
-- Structure de la table `dataholder`
--

CREATE TABLE IF NOT EXISTS `dataholder` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uuid` binary(16) NOT NULL DEFAULT '\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0',
  `_key` varchar(32) NOT NULL DEFAULT '',
  `value` varchar(255) NOT NULL DEFAULT '',
  `expires` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `uuidkey` (`uuid`,`_key`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=283766 ;

--
-- Contenu de la table `dataholder`
--

INSERT INTO `dataholder` (`id`, `uuid`, `_key`, `value`, `expires`) VALUES(144110, '���!|F���4��I��', 'gadgets', 'aucun', NULL);

-- --------------------------------------------------------

--
-- Structure de la table `uhgestion_groups`
--

CREATE TABLE IF NOT EXISTS `uhgestion_groups` (
  `id` varchar(20) NOT NULL DEFAULT '',
  `name` varchar(30) DEFAULT NULL,
  `team_prefix` varchar(16) DEFAULT NULL,
  `team_suffix` varchar(16) DEFAULT NULL,
  `chat_prefix` varchar(50) DEFAULT NULL,
  `chat_suffix` varchar(50) DEFAULT NULL,
  `prefix` varchar(50) DEFAULT NULL,
  `suffix` varchar(50) DEFAULT NULL,
  `color` varchar(10) DEFAULT NULL,
  `inherit` varchar(20) DEFAULT NULL,
  `weight` int(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=TokuDB DEFAULT CHARSET=utf8 `compression`='tokudb_zlib';

--
-- Contenu de la table `uhgestion_groups`
--
INSERT INTO `uhgestion_groups` (`id`, `name`, `team_prefix`, `team_suffix`, `chat_prefix`, `chat_suffix`, `prefix`, `suffix`, `color`, `inherit`, `weight`) VALUES('admin', 'Administrateur', '&c[Admin] ', '', '&c[Administrateur] ', '&f: ', '&c[Administrateur] ', '&f: ', '&c', 'dev', 1000);
INSERT INTO `uhgestion_groups` (`id`, `name`, `team_prefix`, `team_suffix`, `chat_prefix`, `chat_suffix`, `prefix`, `suffix`, `color`, `inherit`, `weight`) VALUES('default', 'Joueur', '&r&7', '', '&r&7', '&7: ', '&r&7', '&7: ', '&7', NULL, 1);
INSERT INTO `uhgestion_groups` (`id`, `name`, `team_prefix`, `team_suffix`, `chat_prefix`, `chat_suffix`, `prefix`, `suffix`, `color`, `inherit`, `weight`) VALUES('dev', 'Développeur', '&c[Développeur] ', '', '&c[Développeur] ', '&f: ', '&c[Développeur] ', '&f: ', '&c', 'respmodo', 600);


--
-- Structure de la table `uhgestion_permissions`
--

CREATE TABLE IF NOT EXISTS `uhgestion_permissions` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` varchar(20) NOT NULL DEFAULT '',
  `permission` varchar(150) NOT NULL DEFAULT '',
  `servers` varchar(255) NOT NULL DEFAULT '*',
  PRIMARY KEY (`id`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=282 ;

--
-- Contenu de la table `uhgestion_permissions`
--

INSERT INTO `uhgestion_permissions` (`id`, `group_id`, `permission`, `servers`) VALUES(1, 'default', 'bungee.reply', '*');
INSERT INTO `uhgestion_permissions` (`id`, `group_id`, `permission`, `servers`) VALUES(2, 'default', 'bungee.help', '*');
INSERT INTO `uhgestion_permissions` (`id`, `group_id`, `permission`, `servers`) VALUES(3, 'default', 'bungee.ignore', '*');
INSERT INTO `uhgestion_permissions` (`id`, `group_id`, `permission`, `servers`) VALUES(4, 'default', 'bungee.msg', '*');
INSERT INTO `uhgestion_permissions` (`id`, `group_id`, `permission`, `servers`) VALUES(5, 'default', 'bungee.party.use', '*');
INSERT INTO `uhgestion_permissions` (`id`, `group_id`, `permission`, `servers`) VALUES(6, 'default', 'bungeecord.server.*', '*');

--
-- Structure de la table `uhgestion_users`
--

CREATE TABLE IF NOT EXISTS `uhgestion_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(38) NOT NULL DEFAULT '',
  `group_id` varchar(20) NOT NULL DEFAULT '',
  `until` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=TokuDB  DEFAULT CHARSET=utf8 `compression`='tokudb_zlib' AUTO_INCREMENT=3164 ;

--
-- Structure de la table `uhgestion_wallet`
--

CREATE TABLE IF NOT EXISTS `uhgestion_wallet` (
  `uuid` varchar(36) NOT NULL DEFAULT '',
  `money` decimal(10,2) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=TokuDB DEFAULT CHARSET=utf8 `compression`='tokudb_zlib';


/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;