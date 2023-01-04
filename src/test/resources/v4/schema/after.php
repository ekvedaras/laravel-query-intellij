<?php Schema::table('users', function (\Illuminate\Database\Schema\Blueprint $table) {
    $table->string('new')->after('<caret>');
});
