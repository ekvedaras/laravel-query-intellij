<?php

class SomeClass {
    public static function someMethod() { return ''; }
}

DB::table('users')
    ->where('email', '!=', '"' . SomeClass::someMethod() . '"')
    ->update(['email' => '"' .SomeClass::someMethod()  . '"']);
