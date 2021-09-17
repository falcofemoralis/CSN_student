<?php

/**
 * Созданние JWT токена
 * @return token - созданный токен в формате JWT
 */
function createJWT($id)
{
    global $SECRET_KEY;

    $header = json_encode(array(
        'type' => 'JWT',
        'alg' => 'HS256'
    ));
    $header = base64_encode($header);

    $body = json_encode(array(
        'iss' => 'BSL_Community',
        'sub' => 'authorization',
        'id' => $id
    ));
    $body = base64_encode($body);

    $signature = hash_hmac('SHA256', "$header.$body", $SECRET_KEY);
    $token = "$header.$body.$signature";
    return $token;
}

/**
 * Проверка авторизации юзера. Берет токен из куки и проверяет его корректность
 * Если авторизация не пройдена - происходит редирект на форму логина
 */
function checkAuth()
{
    global $SECRET_KEY;
    $headers = getallheaders();
    $token = $headers['Token'];

    $parts = explode('.', $token);

    if ($token !== null && count($parts) != 3) {
        http_response_code(401);
        die();
    }

    $testSignature = hash_hmac('SHA256', "$parts[0].$parts[1]", $SECRET_KEY);

    if ($testSignature !== $parts[2]) {
        http_response_code(401);
        die();
    }
    $body = json_decode(base64_decode($parts[1]));
    $id = $body->id;
    return $id;
}