<?php

use SendGrid\Content;
use SendGrid\Email;
use SendGrid\Mail;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\Validator\Constraints as Assert;

//Request::setTrustedProxies(array('127.0.0.1'));

/**
 * Homepage
 */
$app->get('/', function () use ($app) {
    return $app['twig']->render('index.html.twig', array(
                'GOOGLE_API' => getenv('GOOGLE_API')
    ));
});

/**
 * Entry saved to DB. Show please-confirm page.
 */
$app->get('/please-confirm', function () use ($app) {
    return $app['twig']->render('please-confirm.html.twig', array());
});

/**
 * Entry saved to DB. Show please-confirm page.
 */
$app->get('/wrong-input', function () use ($app) {
    return $app['twig']->render('wrong-input.html.twig', array());
});

/**
 * Confirm a notification entry
 */
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

/**
 * Entry confirmed. Show success message.
 */
$app->get('/confirmed/{hash}', function ($hash) use ($app) {

    $notification = $app['db']->fetchAssoc('SELECT * FROM notifications WHERE hash = ?', array($hash));

    if (!$notification) {
        return new Response('Invalid hash.', 500);
    }

    return $app['twig']->render('confirmed.html.twig', array('notification' => $notification));
});

/**
 * Trigger notification for ID
 */
$app->get('/trigger/{hash}', function ($hash) use ($app) {

    $notification = $app['db']->fetchAssoc('SELECT * FROM notifications WHERE hash = ?', array($hash));

    if (!$notification) {
        return new Response('Invalid hash.', 500);
    }

    $url = 'http://' . getenv('CAMEL_HOST') . ':' . getenv('CAMEL_PORT') . '/api/notify';
    $headers = array('Content-Type' => 'application/json', 'Accept' => 'application/json');
    $response = Requests::post($url, $headers, json_encode($notification));

    var_dump($response->body);
    var_dump($response->status_code);
    var_dump($response->headers['content-type']);

    exit;
});

/**
 * Save new entry to DB. Send confirmation mail to mail address
 */
$app->post('/save', function (Request $request) use ($app) {


    $constraint = new Assert\Collection(array(
        'email' => new Assert\Email(),
        'notification_time' => new Assert\Time(),
        'location_start' => new Assert\NotBlank(),
        'location_start_lat' => new Assert\NotBlank(),
        'location_start_lng' => new Assert\NotBlank(),
        'location_end' => new Assert\NotBlank(),
        'location_end_lat' => new Assert\NotBlank(),
        'location_end_lng' => new Assert\NotBlank(),
    ));
    if (substr_count(':', $_POST['notification_time']) < 2) {
        $_POST['notification_time'] .= ':00';
    }
    $errors = $app['validator']->validate($_POST, $constraint);

    if (count($errors) > 0) {
        return $app->redirect('/wrong-input');
    }

    $randomHash = generateRandomString();

    $sql = 'INSERT INTO notifications (email, notification_time, location_start, location_start_lat, location_start_lng, location_end, location_end_lat, location_end_lng, confirmed, hash) VALUES (:email, :notification_time, :location_start, :location_start_lat, :location_start_lng, :location_end, :location_end_lat, :location_end_lng, :confirmed, :hash)';
    $stmt = $app['db']->prepare($sql);
    $stmt->bindValue('email', $request->request->get('email'));
    $stmt->bindValue('notification_time', $request->request->get('notification_time'));
    $stmt->bindValue('location_start', $request->request->get('location_start'));
    $stmt->bindValue('location_start_lat', $request->request->get('location_start_lat'));
    $stmt->bindValue('location_start_lng', $request->request->get('location_start_lng'));
    $stmt->bindValue('location_end', $request->request->get('location_end'));
    $stmt->bindValue('location_end_lat', $request->request->get('location_end_lat'));
    $stmt->bindValue('location_end_lng', $request->request->get('location_end_lng'));
    $stmt->bindValue('confirmed', 0);
    $stmt->bindValue('hash', $randomHash);
    $result = $stmt->execute();

    $message = 'Klik op onderstaande link om aanmelding te bevestigen.<br>';
    $message .= 'http://' . getenv('HOST') . '/confirm/' . $randomHash;

    $from = new Email(null, "test@example.com");
    $to = new Email(null, $request->request->get('email'));
    $content = new Content("text/html", $message);
    $mail = new Mail($from, "Bevestig je aanmelding", $to, $content);
    $sg = new SendGrid(getenv('SENDGRID_API'));
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
