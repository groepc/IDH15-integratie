<?php

date_default_timezone_set('Europe/Amsterdam');
ini_set('display_errors', true);
require_once __DIR__ . '/../vendor/autoload.php';
$app = require __DIR__ . '/../src/app.php';
require __DIR__ . '/../config/prod.php';
require __DIR__ . '/../src/helpers.php';
require __DIR__ . '/../src/controllers.php';
$app->run();
