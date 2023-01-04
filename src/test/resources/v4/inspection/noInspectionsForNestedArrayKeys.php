<?php

class User extends \Illuminate\Database\Eloquent\Model {
}

$data = [
    'value_one' => 1,
    'response' => [
        'deep_value' => 2,
    ],
];

User::create([
    'value_one' => $data['value_one'],
    'deep_value' => $data['response']['deep_value'],
]);
