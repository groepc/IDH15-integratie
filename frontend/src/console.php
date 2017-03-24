<?php

use Symfony\Component\Console\Application;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;
use Symfony\Component\Console\Input\InputArgument;
use Symfony\Component\Console\Input\InputOption;

$console = new Application('Is het fiets weer', 'n/a');
$console->getDefinition()->addOption(new InputOption('--env', '-e', InputOption::VALUE_REQUIRED, 'The Environment name.', 'dev'));
$console->setDispatcher($app['dispatcher']);
$console
        ->register('trigger-camel')
        ->setDefinition(array(
                // new InputOption('some-option', null, InputOption::VALUE_NONE, 'Some help'),
        ))
        ->setDescription('Triggers camel to send e-mails based on the actuel weather')
        ->setCode(function (InputInterface $input, OutputInterface $output) use ($app) {


            $notifications = $app['db']->fetchAll("SELECT * FROM notifications WHERE DATE_FORMAT(concat_ws(' ', CURDATE(), notification_time ), '%H:%i')=DATE_FORMAT(NOW(), '%H:%i')");
            print_r($notifications);
            foreach ($notifications as $notification) {
                if (!$notification) {
                    return new Response('Invalid hash.', 500);
                }

                $url = 'http://' . getenv('CAMEL_HOST') . ':' . getenv('CAMEL_PORT') . '/api/notify';
                $headers = array('Content-Type' => 'application/json', 'Accept' => 'application/json');
                $response = Requests::post($url, $headers, json_encode($notification));
                print_r($response);
            }
        });

return $console;
