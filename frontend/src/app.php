<?php

use Silex\Application;
use Silex\Provider\AssetServiceProvider;
use Silex\Provider\TwigServiceProvider;
use Silex\Provider\ServiceControllerServiceProvider;
use Silex\Provider\HttpFragmentServiceProvider;
use Silex\Provider\DoctrineServiceProvider;
use Silex\Provider\VarDumperServiceProvider;
use Silex\Provider\SwiftmailerServiceProvider;

$dotenv = new Dotenv\Dotenv(__DIR__ . '/..');
$dotenv->load();
$dotenv->required(['DB_HOST', 'DB_NAME', 'DB_USER', 'DB_PASS']);

$app = new Application();
$app->register(new ServiceControllerServiceProvider());
$app->register(new AssetServiceProvider());
$app->register(new TwigServiceProvider());
$app->register(new HttpFragmentServiceProvider());
$app->register(new VarDumperServiceProvider());
$app->register(new SwiftmailerServiceProvider());

$app['twig'] = $app->extend('twig', function ($twig, $app) {
    // add custom globals, filters, tags, ...

    return $twig;
});

$app->register(new DoctrineServiceProvider(), array(
    'db.options' => array(
        'driver'    => 'pdo_mysql',
        'host'      => getenv('DB_HOST'),
        'dbname'    => getenv('DB_NAME'),
        'user'      => getenv('DB_USER'),
        'password'  => getenv('DB_PASS')
    ),
));



return $app;
