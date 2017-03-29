<?php

class HelpersTest extends \PHPUnit_Framework_TestCase
{
    public function testRandomString()
    {
        require __DIR__.'/../src/helpers.php';

        $string = generateRandomString();
        $this->assertEquals(strlen($string), 25);

        $string = generateRandomString(10);
        $this->assertEquals(strlen($string), 10);
    }
}