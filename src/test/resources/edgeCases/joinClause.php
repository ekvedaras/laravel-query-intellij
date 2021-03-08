<?php

class User extends \Illuminate\Database\Eloquent\Model {

}

User::where('email', 'some@email.com')->join('customers', function (\Illuminate\Database\Query\JoinClause $customers) {
    $customers->on('customers.<caret>');
});
