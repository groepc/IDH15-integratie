<?php

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;

//Request::setTrustedProxies(array('127.0.0.1'));

$app->get('/', function () use ($app) {
    return $app['twig']->render('index.html.twig', array(
                'GOOGLE_API' => getenv('GOOGLE_API')
    ));
});

$app->get('/please-confirm', function () use ($app) {
    return $app['twig']->render('please-confirm.html.twig', array());
});

$app->get('/confirm/{hash}', function ($hash) use ($app) {

    // fetch given notificationID by hash
    $userId = $app['db']->fetchColumn('SELECT id FROM notifications WHERE hash = ? AND confirmed = 0', array($hash), 0);

    // if this is not a valid hash
    if (!$userId) {
        return new Response('Invalid hash.', 500);
    }

    // update confirmed status
    $confirmed = $app['db']->executeUpdate('UPDATE notifications SET confirmed = ? WHERE id = ?', array(1, $userId));

    if (!$confirmed) {
        return new Response('Could not confirm notification.', 500);
    }

    return $app->redirect('/confirmed/' . $hash);
});

$app->get('/confirmed/{hash}', function ($hash) use ($app) {

    $notification = $app['db']->fetchAssoc('SELECT * FROM notifications WHERE hash = ?', array($hash));

    if (!$notification) {
        return new Response('Invalid hash.', 500);
    }

    return $app['twig']->render('confirmed.html.twig', array('notification' => $notification));
});

$app->post('/save', function () use ($app) {

    $randomHash = generateRandomString();

    $sql = 'INSERT INTO notifications (email, notification_time, location_start, location_start_lat, location_start_lng, location_end, location_end_lat, location_end_lng, confirmed, hash) VALUES (:email, :notification_time, :location_start, :location_start_lat, :location_start_lng, :location_end, :location_end_lat, :location_end_lng, :confirmed, :hash)';
    $stmt = $app['db']->prepare($sql);
    $stmt->bindValue('email', $_POST['email']);
    $stmt->bindValue('notification_time', $_POST['notification_time']);
    $stmt->bindValue('location_start', $_POST['location_start']);
    $stmt->bindValue('location_start_lat', $_POST['location_start_lat']);
    $stmt->bindValue('location_start_lng', $_POST['location_start_lng']);
    $stmt->bindValue('location_end', $_POST['location_end']);
    $stmt->bindValue('location_end_lat', $_POST['location_end_lat']);
    $stmt->bindValue('location_end_lng', $_POST['location_end_lng']);
    $stmt->bindValue('confirmed', 0);
    $stmt->bindValue('hash', $randomHash);
    $result = $stmt->execute();

    $message = 'Klik op onderstaande link om aanmelding te bevestigen.<br>';
    $message .= 'http://' . getenv('HOST') . '/confirm/' . $randomHash;

    $from = new SendGrid\Email(null, "test@example.com");
    $to = new SendGrid\Email(null, $_POST['email']);
    $content = new SendGrid\Content("text/html", $message);
    $mail = new SendGrid\Mail($from, "Bevestig je aanmelding", $to, $content);
    $sg = new \SendGrid(getenv('SENDGRID_API'));
    $sg->client->mail()->send()->post($mail);

    return $app->redirect('/please-confirm');
});

$app->error(function (\Exception $e, Request $request, $code) use ($app) {
    if ($app['debug']) {
        return;
    }

    // 404.html, or 40x.html, or 4xx.html, or error.html
    $templates = array(
        'errors/' . $code . '.html.twig',
        'errors/' . substr($code, 0, 2) . 'x.html.twig',
        'errors/' . substr($code, 0, 1) . 'xx.html.twig',
        'errors/default.html.twig',
    );

    return new Response($app['twig']->resolveTemplate($templates)->render(array('code' => $code)), $code);
});
