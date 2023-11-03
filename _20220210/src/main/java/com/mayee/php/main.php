<?php
// PHP 下载地址 https://windows.php.net/download#php-7.4-ts-vc15-x64
// 自动加载 class . https://www.php.net/manual/zh/language.oop5.autoload.php
spl_autoload_register(function ($class_name) {
//    var_dump($class_name);
    require_once $class_name . '.php';
});

use Encryption\Encrypter;

$key = 'YqrxNCQvKu5hnHu1qrEb1EzmSagWSzEM12aBASV+kG4=';
$arr = [
    'name' => 'bobby',
    'id' => 1,
    'age' => 26
];
$origin = json_encode($arr);

$cipher = new Encrypter(base64_decode($key), 'AES-256-CBC');
$enc_str = $cipher->encrypt($origin);
echo '密文：' . $enc_str . PHP_EOL;
$dec_str = $cipher->decrypt($enc_str . PHP_EOL);
echo '原文：' . $dec_str . PHP_EOL;

$demo = new DemoClass('Bobby', 27);
$demo->hello();