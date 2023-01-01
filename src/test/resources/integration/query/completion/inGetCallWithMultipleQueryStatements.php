<?php

$query = \DB::query()->from('users');

$query->get('<caret>');
