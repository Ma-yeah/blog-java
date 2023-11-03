<?php

// PHP 面向对象  https://www.runoob.com/php/php-oop.html
class DemoClass
{
    private $name;
    private $age;

    public function __construct($name = '', $age = '')
    {
//        var_dump('类初始化: ' . __CLASS__ . PHP_EOL);
//        print_r('类初始化: ' . __CLASS__ . PHP_EOL);
//        echo '类初始化: ' . __CLASS__ . PHP_EOL;
        print '类初始化: ' . __CLASS__ . PHP_EOL;

        $this->name = $name;
        $this->age = $age;
        if ($age <= 0) {
            throw new RuntimeException('age 必须大于0');
        }
    }

    public function __destruct()
    {
        print '类销毁: ' . __CLASS__ . PHP_EOL;
    }

    public function hello()
    {
        echo 'hello! I am ' . $this->name . 'my age is ' . $this->age . PHP_EOL;
    }
}